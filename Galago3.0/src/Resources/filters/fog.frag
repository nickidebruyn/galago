uniform sampler2D m_Texture;
uniform float m_FogStartDistance;
uniform float m_FogMaxDistance;
uniform float m_FogDensity;
uniform vec4 m_FogColor;

varying vec2 texCoord;
varying float dist;

const float LOG2 = 1.442695;

void main() {

    vec4 finalColor = texture2D(m_Texture, texCoord);

    if (dist > m_FogStartDistance) {
        float fogFactor = (dist - m_FogStartDistance) / (m_FogMaxDistance - m_FogStartDistance);
        fogFactor = clamp(fogFactor, 0.0, m_FogDensity);
        gl_FragColor = mix(finalColor, m_FogColor, fogFactor);
    } else {
        gl_FragColor = finalColor;
    }
    

}