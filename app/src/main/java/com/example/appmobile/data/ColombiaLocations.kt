package com.example.appmobile.data

import android.content.Context
import com.example.appmobile.R
import org.json.JSONObject

data class Department(
    val code: String,
    val name: String,
    val municipalities: List<String>
)

object ColombiaLocations {

    @Volatile
    private var cachedDepartments: List<Department>? = null

    /**
     * Returns the list of departments, loading and caching from JSON on first call.
     */
    fun getDepartments(context: Context): List<Department> {
        val current = cachedDepartments
        if (current != null) return current

        val loaded = loadFromJson(context)
        cachedDepartments = loaded
        return loaded
    }

    /**
     * Returns the municipalities of the department with the given name.
     */
    fun getMunicipalitiesForDepartment(
        context: Context,
        departmentName: String
    ): List<String> {
        val departments = getDepartments(context)
        val dep = departments.firstOrNull { it.name == departmentName }
        return dep?.municipalities ?: emptyList()
    }

    private fun loadFromJson(context: Context): List<Department> {
        val inputStream = context.resources.openRawResource(
            R.raw.colombia_departments_municipalities
        )
        val jsonText = inputStream.bufferedReader().use { it.readText() }

        val root = JSONObject(jsonText)
        val departmentsJson = root.getJSONArray("departments")

        val result = mutableListOf<Department>()

        for (i in 0 until departmentsJson.length()) {
            val depObj = departmentsJson.getJSONObject(i)
            val code = depObj.getString("code")
            val name = depObj.getString("name")
            val municipalitiesJson = depObj.getJSONArray("municipalities")

            val municipalities = mutableListOf<String>()
            for (j in 0 until municipalitiesJson.length()) {
                municipalities.add(municipalitiesJson.getString(j))
            }

            result.add(
                Department(
                    code = code,
                    name = name,
                    municipalities = municipalities
                )
            )
        }

        return result
    }
}
