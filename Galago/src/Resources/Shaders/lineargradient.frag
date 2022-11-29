varying vec2 texCoord;

uniform sampler2D m_ColorMap;
uniform vec4 m_StartColor;
uniform vec4 m_EndColor;
uniform float m_Angle;
uniform float m_MinStep;
uniform float m_MaxStep;

void main(){

    float currentAngle = m_Angle;
    
    vec2 uv = texCoord.xy;
    
    vec2 origin = vec2(0.5, 0.5);
    uv -= origin;
    
    float angle = radians(90.0) - radians(currentAngle) + atan(uv.y, uv.x);

    float len = length(uv);
    uv = vec2(cos(angle) * len, sin(angle) * len) + origin;
	    
    gl_FragColor = mix(m_StartColor, m_EndColor, smoothstep(m_MinStep, m_MaxStep, uv.x));

}