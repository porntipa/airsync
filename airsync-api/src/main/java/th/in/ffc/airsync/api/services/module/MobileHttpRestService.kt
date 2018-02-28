package th.`in`.ffc.airsync.api.services.module

import th.`in`.ffc.airsync.api.dao.GsonConvert
import th.`in`.ffc.airsync.api.services.Store
import th.`in`.ffc.airsync.api.websocket.ApiSocket
import th.`in`.ffc.module.struct.obj.MessageSync
import th.`in`.ffc.module.struct.obj.Pcu
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileToken
import th.`in`.ffc.module.struct.obj.mobiletoken.MobileUserAuth
import java.util.*

class MobileHttpRestService : MobileServices {
    override fun getAll(): List<Pcu> {
        val pculist = ArrayList<Pcu>()
        pculist.addAll(Store.store.getAllPcu())
        return pculist
    }

    override fun getMyPcu(ipAddress: String): List<Pcu> {
        val pculist = ArrayList<Pcu>()
        pculist.add(Store.store.findPcuByIpAddress(ipAddress))
        return pculist
    }

    override fun registerMobile(mobileUserAuth: MobileUserAuth): MessageSync {

        val message = MessageSync(UUID.fromString(mobileUserAuth.mobileUuid.toString()), UUID.fromString(mobileUserAuth.pcu.uuid.toString()), 0, 1, GsonConvert.gson.toJson(mobileUserAuth))
        var messageReturn = MessageSync(UUID.randomUUID(), UUID.randomUUID(), -1, -1, "")
        val pcu = Store.store.findPcuByUuid(mobileUserAuth.pcu.uuid)

        sendAndRecive(message, object : MobileServices.OnReceiveListener {
            override fun onReceive(message: String) {
                messageReturn = GsonConvert.gson.fromJson(message, MessageSync::class.java)
                if (messageReturn.status == 200) {
                    val mobileUserAuthReceive = GsonConvert.gson.fromJson(messageReturn.message, MobileUserAuth::class.java)
                    Store.store.registerMobile(MobileToken(UUID.fromString(messageReturn.to.toString()), pcu))
                    println("Register Mobile " + messageReturn.to.toString())
                }
            }

        }, pcu)

        return messageReturn
    }

    override fun sendAndRecive(messageSync: MessageSync, onReceiveListener: MobileServices.OnReceiveListener, pcu: Pcu) {

        val pcu2: Pcu
        println("sendAndRecive")
        println("Pcu = "+pcu.session)

        if (pcu.session.equals("")) {
            pcu2 = Store.store.findMobileByMobileToken(messageSync.from).pcu
        } else {
            pcu2 = pcu
        }
        println("Pcu2 = "+pcu2.session)
        messageSync.to = pcu2.uuid
        val pcuNetwork = ApiSocket.connectionMap.get(pcu2.session)


        if (pcuNetwork != null) {
            var waitReciveData = true
            var count = 0
            ApiSocket.mobileHashMap.put(messageSync.from, object : ApiSocket.onReciveMessage {
                override fun setOnReceiveMessage(message: String) {
                    println("messageReceive")
                    onReceiveListener.onReceive(message)
                    waitReciveData = false
                }
            })
            pcuNetwork.session.remote.sendString(GsonConvert.gson.toJson(messageSync))
            while (waitReciveData && count < 10) {
                count++
                Thread.sleep(2000)
                println("Wait Count " + count)
            }
        }
    }
}
