uniform sampler2D m_Texture;
uniform float m_Amount;

varying vec2 texCoord;

float level( in float value, in float minimum, in float maximum) {
	return minimum / 255.0 + ( maximum - minimum ) * value / 255.0;
}

float gamma( in float value, in float g ) {
	return pow( value, 1.0 / g );
}

void main (void)  {
	vec4 color = texture2D( m_Texture, texCoord );
	float r = color.r;
	float g = color.g;
	float b = color.b;
	r = level( r, 0.0, 255.0 ); 
	g = level( g, 0.0, 184.0 ); 
	b = level( b, 0.0, 113.0 ); 
	r = gamma( r, 1.10 ); 
	g = gamma( g, 0.95 ); 
	b = gamma( b, 1.04 ); 
	r = level( r, 10.0, 240.0 ); 
	g = level( g, 10.0, 240.0 ); 
	b = level( b, 10.0, 240.0 ); 
	r = gamma( r, 0.87 ); 
	g = gamma( g, 0.87 ); 
	b = gamma( b, 0.87 ); 
	float yL = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
	r += yL; g += yL; b += yL;
	gl_FragColor = vec4( r, g, b, color.a );
}