import java.util.*
import kotlin.random.Random


class Identity private constructor(val nume: String, val prenume: String, val telefon: String, val email: String) {
    companion object {
        fun create(nume: String, prenume: String, telefon: String, email: String): Identity {
            return Identity(nume, prenume, telefon, email)
        }

        fun deserialize(msg: ByteArray): Identity {
            val msgString = String(msg)

            val (nume, prenume, telefon, email) = msgString.split(' ', limit = 4)

            return Identity(nume, prenume, telefon, email)
        }

        private fun generateRandomName(): String {
            val names = listOf("Ion", "Marian", "Adi", "Popescu", "Denis", "Abel",
                "Florin", "Andrei", "Alexandru", "Alex",
                "Florian", "Chiva", "Dani", "Dragos", "Cozma")
            return names.random()
        }

        private fun generateRandomPhoneNumber(): String{
            val sb = StringBuilder("+407")
            repeat(8) {
                sb.append(Random.nextInt(10))
            }
            return sb.toString()
        }

        private fun generateEmail(nume: String, prenume: String): String{
            return "${nume.lowercase()}.${prenume.lowercase()}@gmail.com"
        }

        fun generateRandomIdentity(): Identity {
            val nume = generateRandomName()
            val prenume = generateRandomName()
            val phone = generateRandomPhoneNumber()
            val email = generateEmail(nume, prenume)

            return Identity(nume, prenume, phone, email)
        }
    }

    fun serialize(): ByteArray {
        return "$nume $prenume $telefon $email".toByteArray()
    }

    override fun toString(): String {
        return "$nume $prenume $telefon $email"
    }
}

fun main(args: Array<String>){
    val identity = Identity.generateRandomIdentity()

    print(identity)
}