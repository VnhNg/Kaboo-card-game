package service

import entity.*
/**
 * The root service class is responsible for managing services and the entity layer reference.
 * This class acts as a central hub for every other service within the application.
 *
 */
class RootService{

        val gameService = GameService(this)
        val playerService = PlayerService(this)

        /**
         * The currently active game. Can be `null`, if no game has started yet.
         */
        var kaboo : Kaboo? = null

        /**
         * Adds the provided [newRefreshable] to all services connected to this root service
         *
         * @param newRefreshable The [Refreshable] to be added
         */
        private fun addRefreshable(newRefreshable: Refreshable) {
                gameService.addRefreshable(newRefreshable)
                playerService.addRefreshable(newRefreshable)

        }

        /**
         * Adds each of the provided [newRefreshables] to all services
         * connected to this root service
         *
         * @param newRefreshables The [Refreshable]s to be added
         */
        fun addRefreshables(vararg newRefreshables: Refreshable) {
                newRefreshables.forEach { addRefreshable(it) }
        }
}

