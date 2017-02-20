uniform vec2 g_Resolution;
uniform sampler2D m_Texture;
uniform float m_Amount;

varying vec2 texCoord;



vec2 barrelDistortion(vec2 coord, float amt) {	
	vec2 cc = coord - 0.5;
	float dist = dot(cc, cc);
	//return coord + cc * (dist*dist)  * amt;
	return coord + cc * dist * amt;

}

void main() {

	//vec2 uv=(gl_FragCoord.xy/iResolution.xy);

	vec2 uv=(gl_FragCoord.xy/g_Resolution.xy*0.5)+0.25;
	//uv.y +=.1;
	vec4 a1=texture2D(m_Texture, barrelDistortion(texCoord,0.0*m_Amount));
	vec4 a2=texture2D(m_Texture, barrelDistortion(texCoord,0.1*m_Amount));
	vec4 a3=texture2D(m_Texture, barrelDistortion(texCoord,0.2*m_Amount));
	vec4 a4=texture2D(m_Texture, barrelDistortion(texCoord,0.3*m_Amount));
	
	vec4 a5=texture2D(m_Texture, barrelDistortion(texCoord,0.4*m_Amount));
	vec4 a6=texture2D(m_Texture, barrelDistortion(texCoord,0.5*m_Amount));
	vec4 a7=texture2D(m_Texture, barrelDistortion(texCoord,0.6*m_Amount));
	vec4 a8=texture2D(m_Texture, barrelDistortion(texCoord,0.7*m_Amount));
	
	vec4 a9=texture2D(m_Texture, barrelDistortion(texCoord,0.8*m_Amount));
	vec4 a10=texture2D(m_Texture, barrelDistortion(texCoord,0.9*m_Amount));
	vec4 a11=texture2D(m_Texture, barrelDistortion(texCoord,1.1*m_Amount));
	vec4 a12=texture2D(m_Texture, barrelDistortion(texCoord,1.2*m_Amount));

	vec4 tx=(a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12)/12.;
	gl_FragColor = vec4(tx.rgb, tx.a );
	
}