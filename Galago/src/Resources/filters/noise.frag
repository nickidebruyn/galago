uniform sampler2D m_Texture;
uniform float m_Amount;
uniform float m_Speed;
uniform float m_Time;

varying vec2 texCoord;

float random(vec2 n, float offset ){
	//return fract(sin(dot(gl_FragCoord.xyz+seed,scale))*43758.5453);
	return 0.5 - fract(sin(dot(n.xy + vec2( offset, 0.0 ), vec2(12.9898, 78.233)))* 43758.5453);
}

void main() {

	vec4 color = texture2D(m_Texture, texCoord);

	//color += amount * ( .5 - random( vec3( 1. ), length( gl_FragCoord ) + speed * .01 * time ) );
	color += vec4( vec3( m_Amount * random( texCoord, 0.00001 * m_Speed * m_Time ) ), 1. );

	gl_FragColor = color;

}