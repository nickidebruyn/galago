uniform sampler2D m_Texture;
uniform sampler2D m_NoiseTexture;

uniform vec3 m_DistortionOffsets;
uniform float m_DistortionTime;
uniform float m_DistortionFrequency;

uniform vec4 m_Color;
varying vec2 texCoord;

void main() {
      //vec4 texVal = texture2D(m_Texture, texCoord);
      //gl_FragColor = texVal * m_Color;

    float distortion = texture2D(m_NoiseTexture, vec2( (texCoord.t + m_DistortionTime) * m_DistortionFrequency, 0.5)).r;
    vec3 offsets = distortion * m_DistortionOffsets;
 
    vec3 color = vec3 ( 
      texture2D(m_Texture, vec2(texCoord.s + offsets.r, texCoord.t)).r, 
      texture2D(m_Texture, vec2(texCoord.s + offsets.g, texCoord.t)).g,
      texture2D(m_Texture, vec2(texCoord.s + offsets.b, texCoord.t)).b);

    gl_FragColor = vec4(color,1);


}