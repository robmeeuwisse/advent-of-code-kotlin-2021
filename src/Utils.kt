import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/** Return the class of the caller */
inline val callerClass: Class<*>
    get() = Class.forName(Throwable().stackTrace.first().className)

/** Read lines from the given file */
@Suppress("NOTHING_TO_INLINE") // Inlining to stay in caller's stack frame
inline fun readInput(fileName: String) = Path("src", callerClass.packageName, fileName).readLines()

/** Convert string to md5 hash */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
