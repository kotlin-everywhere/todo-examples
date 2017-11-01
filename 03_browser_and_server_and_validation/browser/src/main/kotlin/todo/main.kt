package todo

import com.minek.kotlin.everywhere.kelibs.result.Err
import com.minek.kotlin.everywhere.kelibs.result.Ok
import com.minek.kotlin.everywhere.kelibs.result.Result
import com.minek.kotlin.everywhere.keuix.browser.Cmd
import com.minek.kotlin.everywhere.keuix.browser.html.*
import com.minek.kotlin.everywhere.keuix.browser.runProgram
import org.w3c.dom.Element

val todoCrate = TodoCrate().apply { i("/api") }

@Suppress("unused")
@JsName("main")
fun main(element: Element) {
    runProgram(element, Model(), ::update, ::view, todoCrate.list(Unit, ::ListResult))
}

data class Model(val newTodoName: String = "", val todoList: List<Todo> = listOf())

sealed class Msg
class SetNewTodoName(val name: String) : Msg()
object Add : Msg()
class Complete(val todo: Todo) : Msg()
class Delete(val todo: Todo) : Msg()
class ListResult(val result: Result<String, List<Todo>>) : Msg()
class AddTodoResult(val newTodo: Todo, val result: Result<String, List<String>>) : Msg()
class CompleteTodoResult(val result: Result<String, Unit>) : Msg()
class DeleteTodoResult(val result: Result<String, Unit>) : Msg()

fun update(msg: Msg, model: Model): Pair<Model, Cmd<Msg>?> {
    return when (msg) {
        is SetNewTodoName -> model.copy(newTodoName = msg.name) to null

        Add -> {
            val newTodo = Todo(model.newTodoName, false)
            val errors = Todo.validate(newTodo)
            if (errors.isNotEmpty()) {
                model to Cmd.alert(errors.joinToString("\n"))
            } else {
                model to todoCrate.add(newTodo) { AddTodoResult(newTodo, it) }
            }
        }

        is Complete -> {
            val newTodoList = model.todoList.map { if (it == msg.todo) it.copy(completed = true) else it }
            model.copy(todoList = newTodoList) to todoCrate.completed(msg.todo, ::CompleteTodoResult)
        }

        is Delete -> {
            model.copy(todoList = model.todoList.filterNot { it == msg.todo }) to todoCrate.delete(msg.todo, ::DeleteTodoResult)
        }

        is ListResult -> {
            when (msg.result) {
                is Err -> model to Cmd.alert<Msg>("오류 ${msg.result}")
                is Ok -> model.copy(todoList = msg.result.value) to null
            }
        }

        is AddTodoResult -> {
            when (msg.result) {
                is Err -> model to Cmd.alert("오류 ${msg.result}")
                is Ok -> {
                    if (msg.result.value.isNotEmpty()) {
                        model to Cmd.alert(msg.result.value.joinToString("\n"))
                    } else {
                        model.copy(newTodoName = "", todoList = model.todoList + msg.newTodo) to Cmd.alert("추가하였습니다.")
                    }
                }
            }
        }

        is CompleteTodoResult -> {
            when (msg.result) {
                is Err -> model to Cmd.alert("오류 ${msg.result}")
                is Ok -> model to Cmd.alert("완료하였습니다.")
            }
        }

        is DeleteTodoResult -> {
            when (msg.result) {
                is Err -> model to Cmd.alert("오류 ${msg.result}")
                is Ok -> model to Cmd.alert("삭제하였습니다.")
            }
        }
    }
}

fun view(model: Model): Html<Msg> {
    return Html.div {
        div {
            input(value(model.newTodoName), onInput(::SetNewTodoName))
            button(onClick(Add), text = "추가")
        }

        div {
            ol {
                model.todoList.forEach { todo ->
                    li {
                        val strike = if (todo.completed) "text-decoration:line-through" else ""
                        span(style(strike), text = todo.name)

                        if (!todo.completed) {
                            button(onClick(Complete(todo)), text = "완료")
                        }

                        button(onClick(Delete(todo)), text = "삭제")
                    }
                }
            }
        }
    }
}
