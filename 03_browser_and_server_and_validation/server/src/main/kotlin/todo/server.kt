package todo

import com.minek.kotlin.everywhere.keuse.runServer

fun main(args: Array<String>) {
    val db = mutableListOf<Todo>()

    val todoCrate = TodoCrate()

    todoCrate.list { db }

    todoCrate.add { todo ->
        val errors = Todo.validate(todo)
        if (errors.isNotEmpty()) {
            return@add errors
        }

        if (db.firstOrNull { it.name == todo.name } != null) {
            return@add listOf("${todo.name}은 이미 존재 합니다.")
        }

        db += todo
        listOf()
    }

    todoCrate.completed { todo -> db.replaceAll { if (it == todo) it.copy(completed = true) else it } }

    todoCrate.delete { todo -> db.removeIf { it == todo } }

    todoCrate.runServer(port = 5000)
}