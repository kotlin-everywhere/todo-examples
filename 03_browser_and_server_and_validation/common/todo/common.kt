package todo

import com.minek.kotlin.everywehre.keuson.convert.Converters
import com.minek.kotlin.everywehre.keuson.decode.Decoders
import com.minek.kotlin.everywehre.keuson.decode.map
import com.minek.kotlin.everywehre.keuson.encode.Encoder
import com.minek.kotlin.everywehre.keuson.encode.Encoders
import com.minek.kotlin.everywhere.kelibs.validator.*
import com.minek.kotlin.everywhere.keuse.Crate

class TodoCrate : Crate() {
    val list by e(Converters.unit, Converters.list(Todo.converter))
    val add by e(Todo.converter, Converters.list(Converters.string))
    val completed by e(Todo.converter, Converters.unit)
    val delete by e(Todo.converter, Converters.unit)
}

data class Todo(val name: String, val completed: Boolean) {
    companion object {
        private val encoder: Encoder<Todo> = {
            Encoders.object_("name" to Encoders.string(it.name), "completed" to Encoders.boolean(it.completed))
        }
        private val decoder = map(Decoders.field("name", Decoders.string), Decoders.field("completed", Decoders.boolean), ::Todo)
        val converter = encoder to decoder

        val validate = validator(
                Todo::name to first(
                        ifBlank("TODO 를 입력하여 주십시오."),
                        ifNotBetween("TODO 는 3자이상 30자 이하로 입력 하여 주십시오.", 3, 30)
                )
        )
    }
}

