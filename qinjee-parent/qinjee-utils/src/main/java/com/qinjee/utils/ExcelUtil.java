package com.qinjee.utils;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class ExcelUtil {
    /**
     * @param title
     * 工作簿名称
     * @param heads
     * 表格第一行名称数组
     * @param dates
     * 要导入表格的数据
     * @param url
     * 临时存放excel文件的文件夹
     * 导出excel
     */
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";
    private final static String INTEGER = "Integer";
    private final static String STRING = "String";
    private final static String BOOLEAN = "Boolean";
    private final static String NULL = "";
    private final static String DATE = "Date";
    private final static String SHORT = "Short";
    /**
     * @param path  临时存放excel文件路径
     * @param title 工作簿名称
     * @param heads 表格第一行名称数组
     * @param dates 要写入表格的数据
     *              下载excel
     */
    public static void download(String path, HttpServletResponse response,
                                String title, List<String> heads, List<Map<String, String>> dates, Map<String, String> map) {
        exportExcel(title, heads, dates, path, map);
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename="
                    + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            // 删除d:/test文件夹下面临时存放的文件
            File file2 = new File(path);
            file2.delete();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * @param title 主题
     * @param heads 这个参数是所有字段的内容，做为表头显示
     * @param dates 这个是存储信息的字段，
     *              示例：
     *              Map<String, String> map = new LinkedHashMap<String,String>();
     *              map.put("ID", String.valueOf(i+1));
     *              map.put("Name", "name"+(i+1));
     *              map.put("Pass", "pass"+(i+1));
     *              dates.add(map);
     * @param url   文件存储路径
     * @param map   以表头信息为key，类型为value的Map集合
     */
    private static void exportExcel(String title, List<String> heads,
                                    List<Map<String, String>> dates, String url, Map<String, String> map) {
        // 新建excel报表
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        // 添加一个sheet名称
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(title);

        //设置单元格样式
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        //水平居中
        style.setAlignment(CellStyle.ALIGN_CENTER);
        // 垂直居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        /*
         * 设定合并单元格区域范围 firstRow 0-based lastRow 0-based firstCol 0-based lastCol
         * 0-based
         */
        CellRangeAddress cra = new CellRangeAddress(0, 0, 0, heads.size() - 1);
        // 在sheet里增加合并单元格
        hssfSheet.addMergedRegion(cra);
        HSSFRow row = hssfSheet.createRow(0);
        HSSFCell cell_1 = row.createCell(0);
        cell_1.setCellValue(title);
        //设置样式
        cell_1.setCellStyle(style);
        HSSFRow hssfRow = hssfSheet.createRow(1);
        for (int i = 0; i < heads.size(); i++) {
            HSSFCell hssfCell = hssfRow.createCell(i);
            hssfCell.setCellValue(heads.get(i));
            hssfCell.setCellType(getCellType(map.get(heads.get(i))));
        }

        // 循环将list里面的值取出来放进excel中
        for (int i = 0; i < dates.size(); i++) {
            HSSFRow hssfRow1 = hssfSheet.createRow(i + 2);
            int j = 0;
            Iterator<Map.Entry<String, String>> it = dates.get(i).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                HSSFCell hssfCell = hssfRow1.createCell(j);
                hssfCell.setCellValue(entry.getValue());
                j++;
            }
        }

        FileOutputStream fout = null;
        try {
            // 用流将其写到指定的url
            fout = new FileOutputStream(url);
            hssfWorkbook.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * CellType 类型 值
     * CELL_TYPE_NUMERIC	数值型	0
     * CELL_TYPE_STRING	字符串型	1
     * CELL_TYPE_FORMULA	公式型	2
     * CELL_TYPE_BLANK	空值	3
     * CELL_TYPE_BOOLEAN	布尔型	4
     * CELL_TYPE_ERROR	错误	5
     *
     * @param type
     * @return
     */
    public static Integer getCellType( String type) {
        if (INTEGER.equals(type)) {
            return 0;
        } else if (STRING.equals(type)) {
            return 1;
        } else if (BOOLEAN.equals(type)) {
            return 4;
        } else if (NULL.equals(type)) {
            return 3;
        } else if (DATE.equals(type)) {
            return 0;
        } else if(SHORT.equals(type)){
            return 0;
        }
        return 1;
    }
















    /**
     * 读入excel文件，解析后返回
     *
     * @param file
     * @throws IOException
     */
    public static Map<String,List<String>> readExcel(MultipartFile file)
            throws IOException {
        // 检查文件
        checkFile(file);
        // 获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        // 创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                // 获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                // 获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                // 获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                // 循环除了第一行的所有行
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    // 获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    // 获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    // 获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    // 循环当前行
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                    //此时应该返回一个表属性的list集合，与一个以表头为key，value为数据的map
                }
            }
        }
        //TODO 还要根据字段名匹配，还要存进数据库
        //heads存的是第一行的字段名
       List<String> heads=Arrays.asList(list.get(0));
        //map是以表头为key，List<>
        Map<String, List<String>> map = new HashMap();
        for (int i = 0; i < heads.size(); i++) {
            for (int j = 1; j < list.size(); j++) {
                List<String> list1 = Arrays.asList(list.get(j)[i]);
                map.put(heads.get(i),list1);
            }
        }
        return map;
    }

    /**
     * @param file
     * @throws IOException 检查传过来的文件是不是excel文件
     */
    public static void checkFile(MultipartFile file) throws IOException {
        // 判断文件是否存在
        if (null == file) {
            throw new FileNotFoundException("文件不存在！");
        }
        // 获得文件名
        String fileName = file.getOriginalFilename();
        // 判断文件是否是excel文件
        if (!fileName.endsWith(xls) && !fileName.endsWith(xlsx)) {
            throw new IOException(fileName + "不是excel文件");
        }
    }

    /**
     * @param file
     * @return 创建并返回一个workBook（兼容xls和xlsx）
     */
    public static Workbook getWorkBook(MultipartFile file) {
        // 获得文件名
        String fileName = file.getOriginalFilename();
        // 创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            // 获取excel文件的io流
            InputStream is = file.getInputStream();
            // 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(xls)) {
                // 2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(xlsx)) {
                // 2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
        }
        return workbook;
    }

    /**
     * @param cell
     * @return 根据excel表格中的数据类型和数据值返回相对应的数据值
     */
    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        // 判断数据的类型
        switch (cell.getCellType()) {
            // 数字
            case Cell.CELL_TYPE_NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            // 字符串
            case Cell.CELL_TYPE_STRING:
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            // Boolean
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            // 公式
            case Cell.CELL_TYPE_FORMULA:
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            // 空值
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            // 故障
            case Cell.CELL_TYPE_ERROR:
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }



    public static MultipartFile getMultipartFile(File file) {
        MultipartFile multipartFile = null;
        try {
            FileInputStream fileInput = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fileInput));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipartFile;
    }

//    public static void main(String[] args) throws IOException {
//        MultipartFile multipartFile = null;
//        File file = new File("C:\\Users\\Administrator\\Desktop\\hello.xls");
//        FileInputStream fileInput = new FileInputStream(file);
//        multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(fileInput));
//        List<String[]> list = null;
//        try {
//            list = ExcelUtil.readExcel(multipartFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for (String[] strings : list) {
//            for (int i = 0; i < strings.length; i++) {
//                //这里可以获取excel的数据，然后将数据添加进数据库里面
//                System.out.print(strings[i] + "\t");
//            }
//            System.out.println();
//        }
//    }
}