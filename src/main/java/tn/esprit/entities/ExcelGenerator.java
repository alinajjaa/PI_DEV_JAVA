package tn.esprit.entities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javafx.stage.FileChooser;
import tn.esprit.entities.Produit;

public class ExcelGenerator {

    public void generateExcel(List<Produit> produits) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Product Data");

                // Create header row and apply formatting
                Row headerRow = sheet.createRow(0);
                headerRow.setHeightInPoints(20); // Set header row height
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFont(headerFont);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                for (int i = 0; i < 7; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(getHeaderTitle(i));
                    cell.setCellStyle(headerCellStyle);
                    sheet.setColumnWidth(i, 4000); // Set column width
                }

                // Add product data and apply formatting
                CellStyle dataCellStyle = workbook.createCellStyle();
                dataCellStyle.setAlignment(HorizontalAlignment.LEFT);
                dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                for (int i = 0; i < produits.size(); i++) {
                    Row dataRow = sheet.createRow(i + 1);
                    Produit produit = produits.get(i);

                    dataRow.createCell(0).setCellValue(produit.getId_prod());
                    dataRow.createCell(1).setCellValue(produit.getNom_prod());
                    dataRow.createCell(2).setCellValue(produit.getPrix_prod());
                    dataRow.createCell(3).setCellValue(produit.getDescription_prod());
                    dataRow.createCell(4).setCellValue(produit.getQuantite_prod());
                    dataRow.createCell(5).setCellValue(produit.getImage_prod());
                    dataRow.createCell(6).setCellValue(produit.getNomCategorie());

                    for (int j = 0; j < 7; j++) {
                        dataRow.getCell(j).setCellStyle(dataCellStyle);
                    }
                }

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                    System.out.println("Excel file generated successfully.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getHeaderTitle(int index) {
        switch (index) {
            case 0:
                return "Product ID";
            case 1:
                return "Product Name";
            case 2:
                return "Price";
            case 3:
                return "Description";
            case 4:
                return "Quantity";
            case 5:
                return "Image";
            case 6:
                return "Category";
            default:
                return "";
        }
    }
}