package com.hsf1002.sky.electriccard.utils;

/**
 * Created by hefeng on 18-7-24.
 */

@Deprecated
public class LogTest {

    /*    应用, 广播, 服务的大致流程
    *
07-19 14:14:04.807  1739  1739 D ElectricCardApp: onCreate:

07-19 14:14:04.807  1739  1739 D ElectricCardReceiver: onReceive: boot completed. SavePrefsUtils.readSimcardActivatedFromFile() = false
07-19 14:14:04.807  1739  1739 D ElectricCardService: setServiceAlarm: isOn = true

07-19 14:14:07.530  1739  1739 D ElectricCardService: onCreate:
07-19 14:14:07.540  1739  1739 D ElectricCardService: onStartCommand:
07-19 14:14:07.540  1739  1739 D ElectricCardService: readSimCardOnlineDuration: TYPE_MOBILE
07-19 14:14:07.550  1739  1739 D ElectricCardService: setProviderInfo: IMSI = 460077120916081
07-19 14:14:07.550  1739  1739 D ElectricCardService: setProviderInfo: providerName = CMCC
07-19 14:14:07.550  1739  1739 D ProviderInfo: set duration success : ProviderInfo{name='CMCC', accumulatedDuration=1200000, consistentDuration=1800000}
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : updateDurationFromService: curentTime = 1531980847557 , startTime = 0
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : updateDurationFromService: once online duration = 1531980847 seconds
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : updateDurationFromService: preset onceDuration = 1200 seconds
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : updateDurationFromService: preset totalDuration = 1800 seconds
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : updateDurationFromService: lastOnceDuration = 0 seconds
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : updateDurationFromService: lastTotalDuration = 0 seconds
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : writeSimcardAccumulatedOnlineDuration: current once duration =
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : writeSimcardAccumulatedOnlineDuration: current total duration = 1531980847 seconds
07-19 14:14:07.550  1739  1739 D SavePrefsUtils : updateDurationFromService: write simcard activated flag true
07-19 14:14:07.550  1739  1739 D ElectricCardService: readSimCardOnlineDuration: SimcardActivated.............

07-19 14:14:07.620  1739  1739 D ElectricCardReceiver: onReceive: CONNECTIVITY_ACTION .
07-19 14:14:07.620  1739  1739 D ElectricCardReceiver: onReceive: isSimcardActivated = true, isOperatorSetted = true
07-19 14:14:07.620  1739  1739 D ElectricCardReceiver: mobile connected...................................................

07-19 14:44:09.517  1728  1728 D SavePrefsUtils : updateDurationFromService: curentTime = 1531982649531 , startTime = 1531982623466

07-20 11:37:44.576  1743  1743 D ElectricCardReceiver: onReceive: ACTION_SHUTDOWN .
07-20 11:37:47.419  1743  1743 D ElectricCardReceiver: mobile disconnected....................................................
    *
    * */
}
