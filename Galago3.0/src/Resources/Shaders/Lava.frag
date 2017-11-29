varying vec2 texCoord;

uniform sampler2D m_ColorMap;
uniform float m_LavaSpeed;
uniform vec2 m_TextureScale;
uniform float g_Time;

#define time g_Time*m_LavaSpeed

float hash21(in vec2 n) { 
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

mat2 makem2(in float theta) {
    float c = cos(theta);
    float s = sin(theta);
    return mat2(c,-s,s,c);
}

float noise( in vec2 x ) {
    return texture2D(m_ColorMap, x*0.01).x;
}

vec2 gradn(vec2 p) {
    float ep = 0.09;
    float gradx = noise(vec2(p.x+ep,p.y))-noise(vec2(p.x-ep,p.y));
    float grady = noise(vec2(p.x,p.y+ep))-noise(vec2(p.x,p.y-ep));
    return vec2(gradx,grady);
}

float flow(in vec2 p) {
    float z = 2.0;
    float rz = 0.0;
    vec2 bp = p;

    for (float i= 1.0; i < 7.0;i++ ) {
        //primary flow speed
	p += time*0.6;
	//secondary flow speed (speed of the perceived flow)
	bp += time*1.9;
		
	//displacement field (try changing time multiplier)
	vec2 gr = gradn(i*p*0.34+time*1.0);
		
	//rotation of the displacement field
	gr*=makem2(time*6.0-(0.05*p.x+0.03*p.y)*40.0);
		
	//displace the system
	p += gr*0.5;
		
	//add noise octave
	rz+= (sin(noise(p)*7.0)*0.5+0.5)/z;
		
	//blend factor (blending displaced system with base system)
	//you could call this advection factor (.5 being low, .95 being high)
	p = mix(bp,p,0.77);
		
	//intensity scaling
	z *= 1.4;
	//octave scaling
	p *= 2.0;
	bp *= 1.9;
    }
    return rz;	
}


void main() {
    
        vec2 res = m_TextureScale;	
	vec2 p = texCoord.xy / res.xy;
	p.x *= res.x / res.y;
	p *= 5.0;
	float rz = flow(p);

	//vec2 p = texCoord.xy;
	//p.xy -= 0.5;
	//p *= 5.0;
	//float rz = flow(p);
	
	vec3 col = vec3(0.2, 0.07, 0.01)/rz;
	col = pow(col,vec3(1.4));
	gl_FragColor = vec4(col, 1.0);

}