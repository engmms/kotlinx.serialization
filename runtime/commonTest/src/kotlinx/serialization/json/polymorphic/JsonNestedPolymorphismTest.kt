/*
 * Copyright 2017-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.json.polymorphic

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonTestBase
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test

class JsonNestedPolymorphismTest : JsonTestBase() {

    private val polymorphicJson = Json {
        unquoted = true
        serialModule = SerializersModule {
        polymorphic(Any::class, InnerBase::class) {
            addSubclass(InnerImpl.serializer())
            addSubclass(InnerImpl2.serializer())
        }

        polymorphic(Any::class) {
            addSubclass(OuterImpl.serializer())
        }
    }
    }

    @Serializable
    internal data class NestedGenericsList(val list: List<List<@Polymorphic Any>>)

    @Test
    fun testAnyList() = assertJsonFormAndRestored(
        NestedGenericsList.serializer(),
        NestedGenericsList(listOf(listOf(InnerImpl(1)), listOf(InnerImpl(2)))),
        "{list:[[" +
                "{type:kotlinx.serialization.json.polymorphic.InnerImpl,field:1,str:default,nullable:null}],[" +
                "{type:kotlinx.serialization.json.polymorphic.InnerImpl,field:2,str:default,nullable:null}]]}",
        polymorphicJson)

    @Serializable
    internal data class NestedGenericsMap(val list: Map<String, Map<String, @Polymorphic Any>>)

    @Test
    fun testAnyMap() = assertJsonFormAndRestored(
        NestedGenericsMap.serializer(),
        NestedGenericsMap(mapOf("k1" to mapOf("k1" to InnerImpl(1)))),
        "{list:{k1:{k1:{type:kotlinx.serialization.json.polymorphic.InnerImpl,field:1,str:default,nullable:null}}}}",
        polymorphicJson)

    @Serializable
    internal data class AnyWrapper(@Polymorphic val any: Any)

    @Test
    fun testAny() = assertJsonFormAndRestored(
        AnyWrapper.serializer(),
        AnyWrapper(OuterImpl(InnerImpl2(1), InnerImpl(2))),
        "{any:" +
                "{type:kotlinx.serialization.json.polymorphic.OuterImpl,base:{type:kotlinx.serialization.json.polymorphic.InnerImpl2,field:1}," +
                "base2:{type:kotlinx.serialization.json.polymorphic.InnerImpl,field:2,str:default,nullable:null}}}",
        polymorphicJson)
}
