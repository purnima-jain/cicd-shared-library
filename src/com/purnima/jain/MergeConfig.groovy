package com.purnima.jain

class MergeConfig {

    static joinFiles(LinkedHashMap file1, LinkedHashMap file2) {
        file2.collect {
            if(it.value instanceof java.util.LinkedHashMap) {
                def mapName = it.key
                if(!file1."${mapName}") {
                    file1 << [(mapName): [] ]
                    file1."${mapName}" = file2."${mapName}"
                } else {
                    def newMap = mergeHashMap(file1."${mapName}", file2."${mapName}")
                    file1."${mapName}" << newMap
                }
            } else if (it.value instanceOf java.util.ArrayList) {
                def listName = it.key
                if(!file1."${listName}") {
                    file1 << [(listName): [] ]
                }
                file2."${listName}".collect { k ->
                    if(!file1."${listName}".find {it == k}) {
                        file1."${listName}" << k
                    }
                }
             } else {
                file1.put(it.key, it.value)
             } 
        }
        return file1
    }

    static mergeHashMap(LinkedHashMap map1, LinkedHashMap map2) {
        def resultMap = map1.clone()
        map2.collect { k, v ->
            if(!(map1.get(k) != null)) {
                resultMap[k] = v
            } else {
                if((map1.get(k) != null) &&
                (map1.get(k) instanceof java.util.LinkedHashMap) &&
                (v instanceof java.util.LinkedHashMap)) {
                    resultMap[k] = mergeHashMap(map1.get(k, [:]), v)
                } else {
                    resultMap[k] = v
                }
            }
        }
        return resultMap
    }

}