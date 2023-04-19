package com.atitus.ex_pubnub

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.UserId
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult

class MainActivity : AppCompatActivity() {

    lateinit var sliderLight: Slider
    lateinit var switchLamp: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sliderLight = findViewById(R.id.sliderLight)

        val pnConfiguration = PNConfiguration(UserId("RicardoUserId"))
        pnConfiguration.subscribeKey = "sub-c-788d886f-ba47-4813-864c-0a64e3f49070"
        pnConfiguration.publishKey = "pub-c-6c6127f8-3780-42f8-a113-c19612f3adfd"
        pnConfiguration.logVerbosity = PNLogVerbosity.BODY

        val pubnub = PubNub(pnConfiguration)

        pubnub.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, status: PNStatus) {}
            override fun message(pubnub: PubNub, message: PNMessageResult) {
                val messageFromPubnb =
                    Gson().fromJson(message.message.asString, MessageFromPubnb::class.java)
                sliderLight.value = messageFromPubnb.value.toFloat()
            }
            override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {}
            override fun signal(pubnub: PubNub, pnSignalResult: PNSignalResult) {}
            override fun uuid(pubnub: PubNub, pnUUIDMetadataResult: PNUUIDMetadataResult) {}
            override fun channel(pubnub: PubNub, pnChannelMetadataResult: PNChannelMetadataResult) {}
            override fun membership(pubnub: PubNub, pnMembershipResult: PNMembershipResult) {}
            override fun messageAction(
                pubnub: PubNub,
                pnMessageActionResult: PNMessageActionResult
            ) {}
            override fun file(pubnub: PubNub, pnFileEventResult: PNFileEventResult) {}
        })

        pubnub.subscribe()
            .channels(listOf("my_channel_listen")) // subscribe to channels information
            .execute()

        switchLamp = findViewById(R.id.switchLamp)
        switchLamp.setOnCheckedChangeListener { _, status ->
            val valueToSend = if (status) 1 else 0
            pubnub.publish()
                .message(valueToSend)
                .channel("my_channel")
                .async { result, status -> Log.e("TAG", "result $result") }
        }
    }
}