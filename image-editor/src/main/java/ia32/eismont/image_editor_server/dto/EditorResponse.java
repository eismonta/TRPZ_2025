package ia32.eismont.image_editor_server.dto;

public class EditorResponse {
    private String status; 
    private String image;  
    private String errorMessage; 

    // Конструктори
    public EditorResponse(String status, String image) {
        this.status = status;
        this.image = image;
    }

    public EditorResponse(String status, String errorMessage, boolean isError) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}