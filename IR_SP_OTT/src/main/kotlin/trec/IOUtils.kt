package trec

import java.io.*
import java.util.*

/**
 * @author tigi
 *
 * trec.IOUtils, existující metody neměňte.
 */
object IOUtils {
    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return list of lines
     */
    fun readLines(inputStream: InputStream?): List<String> {
        requireNotNull(inputStream) { "Cannot locate stream" }
        return try {
            val result: MutableList<String> = ArrayList()
            val br = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var line: String
            while (br.readLine().also { line = it } != null) {
                if (!line.trim { it <= ' ' }.isEmpty()) {
                    result.add(line.trim { it <= ' ' })
                }
            }
            inputStream.close()
            result
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return text
     */
    fun readFile(inputStream: InputStream?): String {
        val sb = StringBuilder()
        requireNotNull(inputStream) { "Cannot locate stream" }
        return try {
            val br = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var line: String
            while (br.readLine().also { line = it } != null) {
                sb.append(line + "\n")
            }
            inputStream.close()
            sb.toString().trim { it <= ' ' }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param list lines of text to save
     */
    fun saveFile(file: File?, list: Collection<String?>) {
        var printStream: PrintStream? = null
        try {
            printStream = PrintStream(FileOutputStream(file), true, "UTF-8")
            for (text in list) {
                printStream.println(text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            printStream?.close()
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param text text to save
     */
    fun saveFile(file: File?, text: String?) {
        var printStream: PrintStream? = null
        try {
            printStream = PrintStream(FileOutputStream(file), true, "UTF-8")
            printStream.println(text)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            printStream?.close()
        }
    }
}