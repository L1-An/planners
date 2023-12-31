package com.bh.planners.core.pojo.level

import java.util.concurrent.CompletableFuture

/**
 * Chemdah
 * ink.ptms.chemdah.module.level.Level
 *
 * @author sky
 * @since 2021/3/8 11:20 下午
 */
class Level(val algorithm: Algorithm, level: Int, experience: Int) {

    var experience = experience
        private set

    var level = level
        private set

    val top: Int
        get() = algorithm.getExp(level).getNow(Int.MAX_VALUE)

    fun setLevel(value: Int): CompletableFuture<Void> {
        level = value
        correct()
        return addExperience(0)
    }

    fun correct() {
        level = level.coerceAtLeast(algorithm.minLevel).coerceAtMost(algorithm.maxLevel)
    }

    fun addLevel(value: Int): CompletableFuture<Void> {
        level += value
        correct()
        return addExperience(0)
    }

    fun setExperience(value: Int): CompletableFuture<Void> {
        experience = value
        return addExperience(0)
    }

    fun addExperience(value: Int): CompletableFuture<Void> {
        if (level >= algorithm.maxLevel) {
            level = algorithm.maxLevel
            algorithm.getExp(level).thenAccept {
                experience = it
            }
            return CompletableFuture.completedFuture(null)
        }
        val future = CompletableFuture<Void>()
        var lvl = level
        var exp = experience + value
        var expNextLevel = 0
        fun getNextLevel() = algorithm.getExp(lvl).thenAccept {
            expNextLevel = if (it <= 0) Int.MAX_VALUE else it
        }

        fun finish() {
            if (lvl >= algorithm.maxLevel) {
                level = algorithm.maxLevel
                experience = expNextLevel
            } else {
                level = lvl
                experience = exp
                // 修正
                if (experience <= 0) {
                    addExperience(experience)
                }
            }
            future.complete(null)
        }

        fun process() {
            getNextLevel().thenAccept {
                if (exp >= expNextLevel) {
                    lvl += 1
                    exp -= expNextLevel
                    process()
                } else {
                    finish()
                }
            }
        }
        process()
        return future
    }
}
