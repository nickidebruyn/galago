uniform sampler2D m_Texture;
uniform float m_Scale;
uniform float m_Threshold;

varying vec2 texCoord;

float kernel = 0.005;

void main() {

    vec4 sum = vec4(0);

    // mess of for loops due to gpu compiler/hardware limitations
    int j=-2;
    for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,texCoord+vec2(i,j)*kernel);
    j=-1;
    for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,texCoord+vec2(i,j)*kernel);
    j=0;
    for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,texCoord+vec2(i,j)*kernel);
    j=1;
    for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,texCoord+vec2(i,j)*kernel);
    j=2;
    for( int i=-2; i<=2; i++) sum+=texture2D(m_Texture,texCoord+vec2(i,j)*kernel);
    sum/=25.0;

    vec4 s=texture2D(m_Texture, texCoord);
    gl_FragColor=s;

    // use the blurred colour if it's bright enough
    if (length(sum)>m_Threshold)
    {
        gl_FragColor +=sum*m_Scale;
    }
}