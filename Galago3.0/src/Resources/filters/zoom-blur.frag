uniform float g_Aspect;
uniform vec2 g_Resolution;

uniform sampler2D m_Texture;
uniform vec2 m_Center;
uniform float m_Strength;

varying vec2 texCoord;

void main() {

	vec4 sum = vec4( 0. );

	vec2 toCenter = m_Center - texCoord * g_Resolution;
	vec2 inc = toCenter * m_Strength / g_Resolution;
	float boost = 2.;

	inc = m_Center / g_Resolution - texCoord;
	
	sum += texture2D( m_Texture, ( texCoord - inc * 4. ) ) * 0.051;
	sum += texture2D( m_Texture, ( texCoord - inc * 3. ) ) * 0.0918;
	sum += texture2D( m_Texture, ( texCoord - inc * 2. ) ) * 0.12245;
	sum += texture2D( m_Texture, ( texCoord - inc * 1. ) ) * 0.1531;
	sum += texture2D( m_Texture, ( texCoord + inc * 0. ) ) * 0.1633;
	sum += texture2D( m_Texture, ( texCoord + inc * 1. ) ) * 0.1531;
	sum += texture2D( m_Texture, ( texCoord + inc * 2. ) ) * 0.12245;
	sum += texture2D( m_Texture, ( texCoord + inc * 3. ) ) * 0.0918;
	sum += texture2D( m_Texture, ( texCoord + inc * 4. ) ) * 0.051;

	gl_FragColor = sum;

}