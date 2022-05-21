package trec

import com.beust.klaxon.*
import trec.data.Document
import trec.data.IDocument
import trec.utils.Logger
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil


/**
 * @author tigi
 *
 * trec.IOUtils, existující metody neměňte.
 */
object IOUtils {

    private val jsonParser = Klaxon()

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return list of lines
     */
    fun readLines(file: File): String? {
        if(!file.exists()){
            Logger.error("readLines -> File on ${file.path} does not exist!")
            return null
        }

        return file.readText(Charset.defaultCharset())
    }

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return text
     */
    private fun readFile(inputStream: InputStream?): String {
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

/**
*	Recursively reads all json documents from the passed folder
*	@param folder folder files with the json documents
*	@returns List of all documents that were found in the folder 
*
*/
    fun readFolder(folder: File): ArrayList<IDocument>{
        val result = arrayListOf<IDocument>()
        if(!folder.exists()){
            Logger.error("readFolder -> Passed folder does not exist!")
            return result
        }
        Logger.debug("readFolder -> loading documents from ${folder.path}")
        //walk -> get all files recursively {including folders]
        val allFiles = folder.walk()
        val part: Int = ceil(allFiles.count().toDouble() / 10.0).toInt()
        var percentage = 0
        allFiles.forEachIndexed {
            i, file ->
            if(i % part == 0){
                Logger.info("readFolder -> $percentage% loaded!")
                percentage += 10
            }

            if(file.isFile){
                val doc = readDocument(file)

                if(doc != null){
                    doc.id = file.path
                    result.add(doc)
                }
            }
        }
        Logger.debug("readFolder -> loading done! ${result.size} docs loaded!")
        return result
    }

/**
*	Reads json document from the passed file
*   @param file passed json file
*	@returns parsed document on success. Null on failure
*/
    private fun readDocument(file: File): IDocument? {
        return jsonParser.parse<Document>(file)
    }
}
