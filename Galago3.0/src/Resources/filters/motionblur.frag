uniform sampler2D m_Texture;
uniform sampler2D m_DepthTexture;
uniform mat4 m_PrevMatrix;
uniform int m_Strength;

varying vec2 texCoord;

void main(void) {
    vec4 zOverW = texture2D(m_DepthTexture, texCoord);
    // H is the viewport position at this pixel in the range -1 to 1.
    vec4 H = vec4(texCoord.x * 2 - 1, (1 - texCoord.y) * 2 - 1, zOverW.g, 1);
    // Transform by the view-projection inverse.
    vec4 D = gl_ProjectionMatrixInverse * H; // gl_ModelViewProjectionMatrixInverse
    // Divide by w to get the world position.
    vec4 worldPos = D / D.w; // vec4(D.w)

    // Current viewport position
    vec4 currentPos = H;
    // Use the world position, and transform by the previous view-projection matrix.
    vec4 previousPos = m_PrevMatrix * worldPos;
    // Convert to nonhomogeneous points [-1,1] by dividing by w.
    previousPos = previousPos / previousPos.w; // vec4(previousPos.w)
    // Use this frame's position and last frame's to compute the pixel velocity.
    vec2 velocity = vec2(currentPos.xy - previousPos.xy) / 2.0;
    //velocity = (velocity + 1.0 ) / 2.0;

    // Get the initial color at this pixel.
    vec4 color = texture2D(m_Texture, texCoord);
  
    //texCoord += velocity;  
    for(int i = 1; i < m_Strength; ++i) {   
        //texCoord += velocity;
        // Sample the color buffer along the velocity vector.  
        vec4 currentColor = texture2D(m_Texture, texCoord + velocity); 
        // Add the current color to our color sum.  
        color += currentColor;
    }  

    // Average all of the samples to get the final blur color.  
    gl_FragColor = previousPos; // color / m_Strength;  

    //gl_FragColor = finalColor;//vec4(velocity.x, velocity.y, color.g, 1.0);
}