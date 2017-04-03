//// GSTai-BSS-FW2.x
//
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.viettel.common.util;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
///**
// *
// * @author Hoang Long
// */
//public class RequestMonitorStore {
//
//    private static Map<String, RequestMonitorStoreItem> store = new HashMap<String, RequestMonitorStoreItem>();
//    private static final int storeSize = ConstConfig.REQUEST_MONITOR_STORE_SIZE;
//
//    public static void pushToStore(RequestMonitorStoreItem item) {
//        if(store.size() > storeSize) {
//            //RequestMonitorStore.clearStore();
//        }
//        store.put(item.getActionName(), item);
//    }
//
//    public static void clearStore() {
//        store.clear();
//    }
//
//    public static Map<String, RequestMonitorStoreItem> getStore() {
//        return store;
//    }
//
//    public static void showStoreToConsole() {
//        String outputStr = "";
//        RequestMonitorStoreItem RMSItem;
//        Set<String> actionNameSet = store.keySet();
//        Iterator actionNameIterator = actionNameSet.iterator();
//        while(actionNameIterator.hasNext()) {
//            RMSItem = store.get((String)actionNameIterator.next());
//            outputStr += "RMSI-[Action: "+RMSItem.getActionName()+"]-[C_Request: "+RMSItem.getContextConcurrentRequest()+"]-[R_Threshold: "+RMSItem.getContextRequestThreshold()+"]-[P_Time: "+RMSItem.getProcessTime()+"]-[L_Date: "+RMSItem.getLogDate()+"]\n";
//        }
//        System.out.println("----s:RequestMonitorStore Log----");
//        System.out.println(outputStr);
//        System.out.println("----e:RequestMonitorStore Log----");
//    }
//}
