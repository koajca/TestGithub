/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.common.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author os_phucpt1
 */
public class ExportExcelUtils {

    private static ExportExcelUtils instance;

    public static ExportExcelUtils getInstance() {
        if (instance == null) {
            synchronized (ExportExcelUtils.class) {
                if (instance == null) {
                    instance = new ExportExcelUtils();
                }
            }
        }
        return instance;
    }

    public InputStream generateMultiSheet(List listData, Map<String, String> mapData, String templateFileName, String reportFileName) throws Exception {
        InputStream result;
        try {
            XLSTransformer transformer = new XLSTransformer();

            List sheetNames = new ArrayList();
            List maps = new ArrayList();
            List tempList = new ArrayList();
            int indexSheet = 0;
            Map map = new HashMap();
            if (listData == null || listData.isEmpty()) {
                this.putDataToMap(mapData, map);

                map.put("list", new ArrayList());
                sheetNames.add("Sheet_" + (indexSheet + 1));
                maps.add(map);
            } else {
                this.putDataToMap(mapData, map);
                for (int i = 0; i < listData.size(); i++) {
                    tempList.add(listData.get(i));
                    if ((i != 0) && ((i + 1) % 65000 == 0)) {
                        map.put("list", tempList);
                        sheetNames.add("Sheet_" + (indexSheet + 1));
                        maps.add(map);
                        //
                        tempList = new ArrayList();
                        map = new HashMap();
                        this.putDataToMap(mapData, map);
                        indexSheet++;
                    }
                }

                if (!tempList.isEmpty()) {
                    map = new HashMap();
                    this.putDataToMap(mapData, map);
                    map.put("list", tempList);
                    sheetNames.add("Sheet_" + (indexSheet + 1));
                    maps.add(map);
                }
            }
            InputStream inputStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(templateFileName);
            HSSFWorkbook resultWorkbook = transformer.transformMultipleSheetsList(inputStream, maps, sheetNames, "map", new HashMap(), 0);
            OutputStream outputStream = new FileOutputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath(reportFileName));
            resultWorkbook.write(outputStream);
            outputStream.close();
            result = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(reportFileName);

        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    private void putDataToMap(Map<String, String> srcMap, Map<String, String> des) throws Exception {
        try {
            String keyTemp;
            String valueTemp;
            for (Map.Entry<String, String> entry : srcMap.entrySet()) {
                keyTemp = entry.getKey();
                valueTemp = entry.getValue();
                if (keyTemp instanceof String && valueTemp instanceof String) {
                    des.put(keyTemp, valueTemp);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
