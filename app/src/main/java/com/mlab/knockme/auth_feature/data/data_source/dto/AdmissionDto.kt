package com.mlab.knockme.auth_feature.data.data_source.dto


import com.google.gson.annotations.SerializedName
import com.mlab.knockme.auth_feature.domain.model.ProgramInfo

data class AdmissionDto(
    @SerializedName("data")
    val data: List<Data> = listOf(),
    @SerializedName("message")
    val message: String = "", // Data Found
    @SerializedName("status")
    val status: Boolean = false // true
) {
    data class Data(
        @SerializedName("active")
        val active: Boolean = false, // true
        @SerializedName("admissionCircularPrograms")
        val admissionCircularPrograms: List<AdmissionCircularProgram> = listOf(),
    ) {
        data class AdmissionCircularProgram(
            @SerializedName("active")
            val active: Boolean = false, // true
            @SerializedName("id")
            val id: Int = 0, // 96
            @SerializedName("program")
            val program: Program = Program()
        ) {
            data class Program(
                @SerializedName("code")
                val code: String = "", // 17
                @SerializedName("id")
                val id: Int = 0, // 8
                @SerializedName("name")
                val name: String = "", // Master of Science in Management Information System
                @SerializedName("programType")
                val programType: ProgramType = ProgramType(),
                @SerializedName("shortName")
                val shortName: String = "" // M.S. in MIS
            ) {
                data class ProgramType(
                    @SerializedName("code")
                    val code: String = "", // 2
                    @SerializedName("id")
                    val id: Int = 0, // 2
                    @SerializedName("name")
                    val name: String = "" // MASTERS
                )
            }
            fun toProgramInfo() = ProgramInfo(
                programId = program.code,
                id = program.id,
                name = program.name,
                shortName = program.shortName,
                typeId = program.programType.id,
                typeName = program.programType.name
            )
        }
    }
}