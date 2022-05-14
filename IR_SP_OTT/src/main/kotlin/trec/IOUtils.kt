package trec

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import trec.data.Document
import trec.utils.Logger
import java.io.*
import java.util.*

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


    fun readFolder(folder: File): ArrayList<Document>{
        val result = arrayListOf<Document>()
        folder.walk().forEach {
            file ->
            if(file.isFile){
                //todo choose approach
                //result.addAll(readDocuments(file))
                ///*
                val doc = readDocument(file)
                if(doc != null){
                    result.add(doc)
                }
                //*/
            }else{
                result.addAll(readFolder(file))
            }
        }
        return result;
    }

    private fun readDocument(file: File): Document? {
        return jsonParser.parse<Document>(file);
    }

    private fun readDocuments(file: File): ArrayList<Document>{
        val resultArray = arrayListOf<Document>()

        JsonReader(StringReader(readFile(file.inputStream()))).use {
            reader -> reader.beginArray {
                var i = 0
                while(reader.hasNext()){
                    ++i
                    val product = jsonParser.parse<Document>(reader)
                    if (product == null) {
                        Logger.error("$i. document from file '${file.name} could not be parsed!'")
                        continue
                    }
                    resultArray.add(product)
                }
            }
        }
        return resultArray
    }
}