package todo

import com.minek.kotlin.everywhere.keuix.browser.html.*
import com.minek.kotlin.everywhere.keuix.browser.runBeginnerProgram
import org.w3c.dom.Element

@Suppress("unused")
@JsName("main")
fun main(element: Element) {
    runBeginnerProgram(element, Model(), ::update, ::view)
}

data class Model(val newTodoName: String = "", val todoList: List<Todo> = listOf())

data class Todo(val name: String, val completed: Boolean)

sealed class Msg
class SetNewTodoName(val name: String) : Msg()
object Add : Msg()
class Complete(val todo: Todo) : Msg()
class Delete(val todo: Todo) : Msg()

fun update(msg: Msg, model: Model): Model {
    return when (msg) {
        is SetNewTodoName -> model.copy(newTodoName = msg.name)

        Add -> model.copy(newTodoName = "", todoList = model.todoList + Todo(model.newTodoName, false))

        is Complete -> {
            val newTodoList = model.todoList.map { if (it == msg.todo) it.copy(completed = true) else it }
            model.copy(todoList = newTodoList)
        }

        is Delete -> {
            model.copy(todoList = model.todoList.filterNot { it == msg.todo })
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
