 
package dataAccess;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import models.Appointment;
import models.BaseEntity;
 
public class Cache {
             
    private static Map<Type, ArrayList<BaseEntity>> cacheMap = new HashMap<>();
    
    public static void clear(Type type){
        if (type == null){
            cacheMap.clear();
        }
        else{
            if (cacheMap.containsKey(type)){
                cacheMap.remove(type);
            }
        }
    }
      
    public static <T> T find(int id, Type type){
        
        if (!cacheMap.containsKey(type)){
            return null;
        }
        
        ArrayList<BaseEntity> cachedObjects = cacheMap.get(type);
 
       BaseEntity entity = cachedObjects.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
       return (T) entity;       
    }
    
    public static <T> ArrayList<T> getList(Type type){
               
        if (!cacheMap.containsKey(type)){
            return null;
        }
        
        ArrayList<T> cachedObjects = (ArrayList<T>) cacheMap.get(type);
        return cachedObjects;
    }
    
    public static void add(BaseEntity entity, Type type){
         
        if (!cacheMap.containsKey(type)){
            return;
        }
        
        ArrayList<BaseEntity> cachedObjects = cacheMap.get(type);
        BaseEntity found = find(entity.getId(), type);
        if (found != null){
            cachedObjects.remove(found);
        }
        cachedObjects.add(entity);
        
    } 
    
    public static void add(ArrayList<BaseEntity> list, Type type){
         
        clear(type); 
        ArrayList<BaseEntity> cachedObjects = new ArrayList<>();
    
        cachedObjects.addAll(list); 
        
        cacheMap.put(type, list);
        
    } 
    
    public static void remove(BaseEntity entity, Type type){
         
        if (!cacheMap.containsKey(type)){
            return;
        }
        
        ArrayList<BaseEntity> cachedObjects = cacheMap.get(type);
        BaseEntity found = find(entity.getId(), type);
        if (found != null){
            cachedObjects.remove(found);
        }  
    }
}
