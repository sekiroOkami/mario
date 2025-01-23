package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;
    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            // open file
            String src = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = src.split("((#type)( )+[a-zA-Z]+)"); // ((#type)( )+[a-zA-Z]+)

            // Find the st pattern after #type 'pattern'
            // the index of beginning of next word
            int index = src.indexOf("#type") + 6;
            int endOfLine = src.indexOf("\r\n", index);
            String firstPattern = src.substring(index, endOfLine).trim();

            // Find the nd pattern
            // find next type
            index = src.indexOf("#type", endOfLine) + 6;
            endOfLine = src.indexOf("\r\n", index);
            String secondPattern = src.substring(index, endOfLine).trim();

            if (firstPattern.equalsIgnoreCase("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equalsIgnoreCase("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern +"'");
            }

            if (secondPattern.equalsIgnoreCase("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equalsIgnoreCase("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + firstPattern +"'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: could not open file for shader: '" +filepath + "'";
        }

        System.out.println("vertexSource = " + vertexSource);
        System.out.println("fragmentSource = " + fragmentSource);
    }

    // compile shader
    public void compile() {

        // ===========================
        // Compile and link shaders
        // ===========================

        int vertexID, fragmentID;

        // First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '\" + filepath +\"'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert  false : "";
        }

        // ==================================================================

        // First load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath +"'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert  false : "";
        }

        // Link shaders and check for error
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for shader program error
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert  false : "";
        }
    }

    public void use() {
        // Bind shader program
        glUseProgram(shaderProgramID);
    }
    public void detach(){
        glUseProgram(0);
    }
}
