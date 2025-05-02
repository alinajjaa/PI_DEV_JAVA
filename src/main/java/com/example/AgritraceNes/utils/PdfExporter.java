package com.example.AgritraceNes.utils;

import com.example.AgritraceNes.Models.Evenement;
import com.example.AgritraceNes.Models.Participant;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfExporter {

    public static void exporterConfirmationParticipation(Evenement evenement, Participant participant, String filePath) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Définir des couleurs personnalisées
            BaseColor primaryColor = new BaseColor(63, 81, 181); // Bleu indigo
            BaseColor labelBgColor = new BaseColor(232, 234, 246); // Bleu clair pour les labels
            BaseColor thanksColor = new BaseColor(76, 175, 80); // Vert
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, primaryColor);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);



            // Titre
            Paragraph title = new Paragraph("Confirmation de Participation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Espacement
            document.add(Chunk.NEWLINE);

            // Tableau des informations
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1.2f, 2.8f});

            // Ajouter les lignes
            addStyledRow(table, "Nom de l'événement :", evenement.getNom(), labelFont, contentFont, labelBgColor);
            addStyledRow(table, "Lieu :", evenement.getLieu(), labelFont, contentFont, labelBgColor);
            addStyledRow(table, "Date de début :", String.valueOf(evenement.getDateDebut()), labelFont, contentFont, labelBgColor);
            addStyledRow(table, "Date de fin :", String.valueOf(evenement.getDateFin()), labelFont, contentFont, labelBgColor);
            addStyledRow(table, "ID du participant :", String.valueOf(participant.getClientId()), labelFont, contentFont, labelBgColor);
            addStyledRow(table, "Date de participation :", String.valueOf(participant.getDateParticipation()), labelFont, contentFont, labelBgColor);
            addStyledRow(table, "Nombre de personnes :", String.valueOf(participant.getNombrePersonnes()), labelFont, contentFont, labelBgColor);

            document.add(table);

            // Message de remerciement
            Paragraph footer = new Paragraph("Merci de votre participation !", new Font(Font.FontFamily.HELVETICA, 13, Font.BOLDITALIC, thanksColor));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    // Méthode avec fond coloré sur la cellule label
    private static void addStyledRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, BaseColor bgColor) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, valueFont));

        // Appliquer fond à la cellule de label
        cell1.setBackgroundColor(bgColor);

        // Supprimer bordures
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);

        // Padding pour lisibilité
        cell1.setPadding(5);
        cell2.setPadding(5);

        table.addCell(cell1);
        table.addCell(cell2);
    }
}
