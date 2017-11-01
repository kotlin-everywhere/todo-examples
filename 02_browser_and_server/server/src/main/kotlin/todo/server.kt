package todo

import com.minek.kotlin.everywhere.keuse.runServer

fun main(args: Array<String>) {
    val db = mutableListOf<Todo>()

    val todoCrate = TodoCrate()

    todoCrate.list { db }

    todoCrate.add { db += it }

    todoCrate.completed { todo -> db.replaceAll { if (it == todo) it.copy(completed = true) else it } }

    todoCrate.delete { todo -> db.removeIf { it == todo } }

    todoCrate.runServer(port = 5000)
}