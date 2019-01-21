#ifdef USE_TEXTURE
uniform sampler2D m_Texture;
varying vec4 texCoord;
#endif

varying vec4 color;

void main(){
    if (color.a <= 0.01)
        discard;
	
	#ifdef POINT_SPRITE
		vec2 uv = mix(texCoord.xy, texCoord.zw, gl_PointCoord.xy);
	#else
		vec2 uv = texCoord.xy;
	#endif
	vec4 tex = texture2D(m_Texture, uv);
	if (tex.a < 0.05)
		discard;
	
	gl_FragColor =  tex;// * color;
}