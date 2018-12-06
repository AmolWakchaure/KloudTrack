package snsystems.obd.docupload;

/**
 * Created by shree on 02-Feb-17.
 */
public class DocumentInformation
{
    private String documentType;
    private String documentImagePath;
    private String documentExpiryDate;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentImagePath() {
        return documentImagePath;
    }

    public void setDocumentImagePath(String documentImagePath) {
        this.documentImagePath = documentImagePath;
    }

    public String getDocumentExpiryDate() {
        return documentExpiryDate;
    }

    public void setDocumentExpiryDate(String documentExpiryDate) {
        this.documentExpiryDate = documentExpiryDate;
    }
}
