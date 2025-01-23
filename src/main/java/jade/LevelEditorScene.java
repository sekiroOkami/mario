package jade;

import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc ="#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "// variable that pass to the fragment Shader\n" +
            "out vec4 fColor; // prefix with f -> fragment\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc ="#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;
    private float[] vertexArray = {
        // -1 left side of screen, 1 right side of screen
        // position                   // color
         0.5f, -0.5f,  0.0f,           1.0f, 0.0f, 0.0f, 1.0f, // Bottom right  0
        -0.5f,  0.5f,  0.0f,           0.0f, 1.0f, 0.0f, 1.0f, // Top left      1
         0.5f,  0.5f,  0.0f,           0.0f, 0.0f, 1.0f, 1.0f, // Top right     2
        -0.5f, -0.5f,  0.0f,           1.0f, 1.0f, 0.0f, 1.0f, // Bottom left   3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
        /*
                1           2

                3           0
         */

        2, 1, 0, // Top right triangle
        0, 1, 3  // bottom left triangle
    };

    private int vaoID, vboID, eboID; // vertexArrayObject, vertexBufferObject, elementBufferObject
    private Shader defaultShader;

    public LevelEditorScene() {

    }

    @Override
    public void init() {

        defaultShader = new Shader(".\\assets\\shaders\\default.glsl");
        defaultShader.compile();

        // ====================================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ====================================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        // it' oriented the correct way for openGL
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        // make sure everything we're doing is for this buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3; // x,y,z
        int colorSize = 4; // rgba
        int floatSizeBytes = 4; // 4 byte for 1 float
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;

        // layout (location=0) in vec3 aPos;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        // layout (location=1) in vec4 aColor;
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();
    }
}
