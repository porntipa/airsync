/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.api.dao

import ffc.model.Pcu
import ffc.model.toJson
import java.util.*

class InMemoryPcuDao : PcuDao {

    private constructor()
    val pcuList = arrayListOf<Pcu>()


    companion object {
        val instance = InMemoryPcuDao()

    }

    override fun findByToken(token: String): Pcu {
        return pcuList.find { it.pcuToken.equals(token) }!!
    }


    override fun insert(pcu: Pcu) {
        if (!pcuList.contains(pcu)) {
            println("Pcu insert InMemoryPcuDao \nPcu data = "+pcu.toJson())
            pcuList.add(pcu)
            println("Test get Pcu Before insert\nPcu data ="+findByUuid(pcu.uuid).toJson())
        }else{
            pcuList.remove(pcu)
            insert(pcu)
        }
    }

    override fun findByUuid(uuid: UUID): Pcu {
        println("findByUuid InMemoryPcuDao \nUUID data = "+uuid)
        val pcu = pcuList.find { it.uuid == uuid }!!
        println("find Result = "+pcu.toJson())
        return pcu
        //return pcuList.find { it.uuid }
    }


    override fun findByIpAddress(ipAddress: String): Pcu {
        return pcuList.find { it.lastKnownIp == ipAddress }!!
    }

    override fun remove(pcu: Pcu) {
        pcuList.remove(pcu)
    }

    override fun find(): List<Pcu> {
        return pcuList.toList()
    }

    override fun updateToken(pcu: Pcu): Pcu {
        val pcuFind = findByUuid(pcu.uuid)
        pcuFind.centralToken = UUID.randomUUID().toString()
        pcuFind.pcuToken = UUID.randomUUID().toString()
        insert(pcuFind)
        return pcuFind
    }
}
