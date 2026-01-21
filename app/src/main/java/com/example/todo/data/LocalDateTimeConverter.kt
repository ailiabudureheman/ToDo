
package com.example.todo.data

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter {
    // 使用标准的ISO格式，日期和时间之间用'T'分隔
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            try {
                // 尝试使用标准ISO格式解析
                return LocalDateTime.parse(it, formatter)
            } catch (e: Exception) {
                // 如果解析失败，尝试替换空格为'T'
                val normalizedValue = it.replace(' ', 'T')
                try {
                    return LocalDateTime.parse(normalizedValue, formatter)
                } catch (e2: Exception) {
                    // 如果仍然失败，尝试其他修复方法
                    if (it.length >= 15) {
                        // 尝试在日期和时间之间添加'T'
                        val formattedValue = if (it.contains(' ')) {
                            it.replace(' ', 'T')
                        } else if (it.length >= 10) {
                            it.substring(0, 10) + 'T' + it.substring(10)
                        } else {
                            it
                        }
                        return LocalDateTime.parse(formattedValue, formatter)
                    }
                    throw e
                }
            }
        }
    }
}
