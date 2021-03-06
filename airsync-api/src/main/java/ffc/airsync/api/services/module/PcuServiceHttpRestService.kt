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

package ffc.airsync.api.services.module

import ffc.airsync.api.dao.DaoFactory
import ffc.model.*
import java.util.*

class PcuServiceHttpRestService : PcuService {

    val pcuDao = DaoFactory().buildPcuDao()
    val mobileUserAuthDao = DaoFactory().buildUserAuthDao()


    override fun register(pcu: Pcu, lastKnownIp: String): Pcu {

        pcu.pcuToken = UUID.randomUUID().toString()
        pcu.centralToken=UUID.randomUUID().toString()
        pcu.lastKnownIp=lastKnownIp
        pcuDao.insert(pcu)
        return pcu
    }

    override fun getMobileUser(pcu: Pcu): List<MobileUserAuth> {
        val mobileUserList = mobileUserAuthDao.findByPcu(pcu)
        return mobileUserList
    }

    override fun setUserPass(userAuth: MobileUserAuth) {
        mobileUserAuthDao.updateStatusPass(userAuth)
    }

    override fun setUserNotPass(userAuth: MobileUserAuth) {
        mobileUserAuthDao.updateStatusNotPass(userAuth)
    }

    override fun sendEventGetData(token: TokenMessage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val pcu = pcuDao.findByToken(token.token)
        if(pcu.session != null) {
            val session = pcu.session
        }
        //

    }

    override fun getData(token: TokenMessage): Message<QueryAction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
