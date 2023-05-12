package com.example.firstapplication.itexttesting2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firstapplication.itexttesting2.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText numberEditText;
    private EditText locationEditText;
    private Button createPdfButton;

    static final int CREATE_FILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.name_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);
        numberEditText = findViewById(R.id.number_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        createPdfButton = findViewById(R.id.create_pdf_button);

        createPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });
    }

    private void createPdf() {
        // Get user input data
        String name = nameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String number = numberEditText.getText().toString();
        String location = locationEditText.getText().toString();

        // Create PDF document
        Document document = new Document();

        // Get current date and time to use in file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Create file name
        String fileName = "UserData_" + timeStamp + ".pdf";

        // Create new file with the specified name in the app's cache directory
        File pdfFile = new File(getCacheDir(), fileName);

        try {
            // Create output stream for PDF file
            FileOutputStream outputStream = new FileOutputStream(pdfFile);

            // Create PDF writer
            PdfWriter.getInstance(document, outputStream);

            // Open document
            document.open();

            // Add user input data to document
            Paragraph nameParagraph = new Paragraph("Name: " + name);
            Paragraph ageParagraph = new Paragraph("Age: " + age);
            Paragraph numberParagraph = new Paragraph("Number: " + number);
            Paragraph locationParagraph = new Paragraph("Location: " + location);

            nameParagraph.setAlignment(Element.ALIGN_LEFT);
            ageParagraph.setAlignment(Element.ALIGN_LEFT);
            numberParagraph.setAlignment(Element.ALIGN_LEFT);
            locationParagraph.setAlignment(Element.ALIGN_LEFT);

            document.add(nameParagraph);
            document.add(ageParagraph);
            document.add(numberParagraph);
            document.add(locationParagraph);

            // Close document
            document.close();

            // Grant permissions to access the PDF file to other apps
            grantUriPermission(getPackageName(), Uri.fromFile(pdfFile), Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Create SAF intent to save the PDF file in external storage
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Start SAF to create the PDF file in external storage
            startActivityForResult(intent, CREATE_PDF_CODE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }// This method is called when the PDF creation is complete

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_PDF_CODE && resultCode == RESULT_OK) {
            // Get the URI of the created PDF file
            Uri uri = data.getData();

            // Get permission to access the created PDF file
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Open the created PDF file using a PDF viewer app
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }

    // Constants
    private static final int CREATE_PDF_CODE = 1;  // Request code for creating the PDF file using SAF
}

