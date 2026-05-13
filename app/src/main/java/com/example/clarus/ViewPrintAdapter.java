package com.example.clarus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewPrintAdapter extends PrintDocumentAdapter {
    private Context context;
    private View view;
    private PrintedPdfDocument pdfDocument;

    public ViewPrintAdapter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        pdfDocument = new PrintedPdfDocument(context, newAttributes);
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        PrintDocumentInfo info = new PrintDocumentInfo.Builder("rapor.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1).build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        PdfDocument.Page page = pdfDocument.startPage(0);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        pdfDocument.finishPage(page);

        try {
            pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            pdfDocument.close();
            pdfDocument = null;
        }
        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
    }
}