package th.`in`.ffc.airsync.api.websocket


import com.google.gson.Gson
import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import th.`in`.ffc.module.struct.Pcu
import java.util.*


class AirSyncSocket : WebSocketAdapter() {
    companion object {
        val gson = Gson()
    }

    var session : String = ""
    var count = 0
    var stage = 0  //stage 0:init   1:run
    var pcu :Pcu= Pcu("","", UUID.randomUUID(),false)



    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        System.out.println("Socket Connected: " + sess)
        this.session= DigestUtils.sha1Hex(sess.toString())
        println("Session= "+this.session)
    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)
        //println("Session "+session)
        println("Stage = "+stage)
        System.out.println("Count:"+(count++)+"\tReceived TEXT message: " + message)
        if(stage==0){
            pcu = gson.fromJson(message,Pcu::class.java)
            println("Pcu Name= "+pcu.Name)
            stage=1

        }else if (stage ==1){
            println(message)
        }

    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)
        System.out.println("Socket Closed: [" + statusCode + "] " + reason)
    }

    override fun onWebSocketError(cause: Throwable?) {
        super.onWebSocketError(cause)
        cause!!.printStackTrace(System.err)
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        super.onWebSocketBinary(payload, offset, len)
    }
}
