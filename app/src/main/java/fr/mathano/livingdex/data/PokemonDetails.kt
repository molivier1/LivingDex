package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi
import co.pokeapi.pokekotlin.PokeApi.Default.get
import co.pokeapi.pokekotlin.model.ChainLink
import co.pokeapi.pokekotlin.model.EvolutionDetail
import co.pokeapi.pokekotlin.model.PokemonSpecies
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.local.DatabaseProvider
import fr.mathano.livingdex.data.local.toDataPokemonDetail
import fr.mathano.livingdex.data.local.toPokemonDetailEntity
import fr.mathano.livingdex.data.model.DataPokemonDetail
import fr.mathano.livingdex.toDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object PokemonDetails {
    private const val POKEAPI_BASE_URL = "https://pokeapi.co/api/v2"

    suspend fun recupererPokemonDetail(idPokemon: Int): DataPokemonDetail = withContext(Dispatchers.IO) {
        val locale = AppLanguage.current()
        val apiLocale = AppLanguage.pokeApiLanguage()
        val pokeDao = DatabaseProvider.pokeDao

        val pokemonEnBase = pokeDao.getPokemonDetail(idPokemon, locale)
        if (pokemonEnBase != null) {
            return@withContext pokemonEnBase.toDataPokemonDetail()
        }

        val pokemon = PokeApi.getPokemonVariety(idPokemon)
        val pokemonSpecies = pokemon.species.get()

        val nom = pokemonSpecies.names.firstOrNull {
            it.language.name.equals(apiLocale, ignoreCase = true)
        }?.name ?: pokemon.name.toDisplayName()

        val types = pokemon.types
            .sortedBy { it.slot }
            .map { typeSlot ->
                async {
                    runCatching {
                        recupererNomType(typeSlot.type.name)
                    }.getOrDefault(typeSlot.type.name.toDisplayName())
                }
            }
            .map { it.await() }

        val talents = pokemon.abilities
            .mapNotNull { pokemonAbility ->
                pokemonAbility.ability?.let { abilityHandle ->
                    pokemonAbility to abilityHandle
                }
            }
            .map { (pokemonAbility, abilityHandle) ->
                async {
                    val nomTalent = abilityHandle.get().names.firstOrNull {
                        it.language.name.equals(apiLocale, ignoreCase = true)
                    }?.name
                        ?: abilityHandle.name.toDisplayName()

                    if (pokemonAbility.isHidden) {
                        AppLanguage.string(R.string.hidden_ability_format, nomTalent)
                    } else {
                        nomTalent
                    }
                }
            }
            .map { it.await() }

        val detail = DataPokemonDetail(
            idPokemon = pokemon.id,
            nom = nom,
            urlSprite = pokemon.sprites.frontDefault.orEmpty(),
            taille = pokemon.height,
            poids = pokemon.weight,
            types = types,
            description = pokemonSpecies.description(apiLocale),
            talents = talents,
            evolutions = runCatching {
                recupererEvolutions(pokemonSpecies)
            }.getOrDefault(emptyList())
        )

        pokeDao.insertPokemonDetail(detail.toPokemonDetailEntity(locale))

        return@withContext detail
    }

    private fun PokemonSpecies.description(locale: String): String? =
        flavorTextEntries
            .firstOrNull { it.language.name == locale }
            ?.flavorText
            ?.replace("\n", " ")
            ?.replace("\u000c", " ")

    private fun recupererNomType(typeName: String): String {
        val response = URL("$POKEAPI_BASE_URL/type/$typeName").readText()
        val names = JSONObject(response).getJSONArray("names")
        val apiLocale = AppLanguage.pokeApiLanguage()

        for (index in 0 until names.length()) {
            val nameEntry = names.getJSONObject(index)
            val language = nameEntry.getJSONObject("language").getString("name")

            if (language.equals(apiLocale, ignoreCase = true)) {
                return nameEntry.getString("name")
            }
        }

        return typeName.toDisplayName()
    }

    private suspend fun recupererEvolutions(
        pokemonSpecies: PokemonSpecies,
    ): List<String> {
        val evolutionChain = pokemonSpecies.evolutionChain.get()
        val currentLink = evolutionChain.chain.findSpecies(pokemonSpecies.name) ?: return emptyList()

        return currentLink.evolvesTo.mapNotNull { nextEvolution ->
            nextEvolution.evolutionDetails.firstOrNull()?.toConditionText()
        }
    }

    private fun ChainLink.findSpecies(speciesName: String): ChainLink? {
        if (species.name == speciesName) {
            return this
        }

        evolvesTo.forEach { nextLink ->
            nextLink.findSpecies(speciesName)?.let { return it }
        }

        return null
    }

    private fun EvolutionDetail.toConditionText(): String {
        val conditions = mutableListOf<String>()

        minLevel?.let { conditions += AppLanguage.string(R.string.evolution_level, it) }
        item?.let { conditions += AppLanguage.string(R.string.evolution_with_item, it.name.toDisplayName()) }
        heldItem?.let { conditions += AppLanguage.string(R.string.evolution_hold_item, it.name.toDisplayName()) }
        knownMove?.let { conditions += AppLanguage.string(R.string.evolution_known_move, it.name.toDisplayName()) }
        knownMoveType?.let {
            conditions += AppLanguage.string(R.string.evolution_known_move_type, it.name.toDisplayName())
        }
        location?.let { conditions += AppLanguage.string(R.string.evolution_at_location, it.name.toDisplayName()) }
        minHappiness?.let { conditions += AppLanguage.string(R.string.evolution_happiness, it) }
        minBeauty?.let { conditions += AppLanguage.string(R.string.evolution_beauty, it) }
        minAffection?.let { conditions += AppLanguage.string(R.string.evolution_affection, it) }
        partySpecies?.let {
            conditions += AppLanguage.string(R.string.evolution_party_species, it.name.toDisplayName())
        }
        partyType?.let { conditions += AppLanguage.string(R.string.evolution_party_type, it.name.toDisplayName()) }
        tradeSpecies?.let {
            conditions += AppLanguage.string(R.string.evolution_trade_species, it.name.toDisplayName())
        }

        if (timeOfDay.isNotBlank()) {
            conditions += timeOfDay.toDisplayName()
        }
        if (needsOverworldRain) {
            conditions += AppLanguage.string(R.string.evolution_rain)
        }
        if (turnUpsideDown) {
            conditions += AppLanguage.string(R.string.evolution_turn_upside_down)
        }

        return conditions.ifEmpty {
            listOf(trigger.name.toDisplayName())
        }.joinToString(", ")
    }

}
