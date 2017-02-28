uniform vec2 g_Resolution;
uniform sampler2D m_Texture;
uniform sampler2D m_DepthTexture;
 
varying vec2 texCoord;

const float isPacked = 0.0;
const float onlyOcclusion = 0.0;

float random(vec3 scale,float seed) {
    return fract(sin(dot(gl_FragCoord.xyz+seed,scale))*43758.5453+seed);
}

float unpack_depth(const in vec4 color) {
    return ( color.r * 256. * 256. * 256. + color.g * 256. * 256. + color.b * 256. + color.a ) / ( 256. * 256. * 256. );
}

float sampleDepth( vec2 uv ) {
    if( isPacked == 1. ) {
	return unpack_depth( texture2D( m_DepthTexture, uv ) );
    } else {
	return texture2D( m_DepthTexture, uv ).r;
    }
}

float occlusion = 0.0;
float depth = 0.0;
float ac = 0.0;

void checkDepth( vec2 uv ) { // from iq's tutorial
    float zd = 10.0 * min( depth - sampleDepth( uv ), 0.0 );
    ac += zd;
    occlusion += 1.0 / ( 1. + zd * zd );
}

void main() {

    depth = sampleDepth(texCoord);
	
    float r = 4.;
    float xi = r / g_Resolution.x;
    float yi = r / g_Resolution.y;

    checkDepth( texCoord + vec2( - 2. * xi, - 2. * yi ) );
    checkDepth( texCoord + vec2(      - xi, - 2. * yi ) );
    checkDepth( texCoord + vec2(        0., - 2. * yi ) );
    checkDepth( texCoord + vec2(        xi, - 2. * yi ) );
    checkDepth( texCoord + vec2(   2. * xi, - 2. * yi ) );

    checkDepth( texCoord + vec2( - 2. * xi, - yi ) );
    checkDepth( texCoord + vec2(      - xi, - yi ) );
    checkDepth( texCoord + vec2(        0., - yi ) );
    checkDepth( texCoord + vec2(        xi, - yi ) );
    checkDepth( texCoord + vec2(   2. * xi, - yi ) );

    checkDepth( texCoord + vec2( - 2. * xi, 0. ) );
    checkDepth( texCoord + vec2(      - xi, 0. ) );
    checkDepth( texCoord + vec2(        xi, 0. ) );
    checkDepth( texCoord + vec2(   2. * xi, 0. ) );

    checkDepth( texCoord + vec2( - 2. * xi, yi ) );
    checkDepth( texCoord + vec2(      - xi, yi ) );
    checkDepth( texCoord + vec2(        0., yi ) );
    checkDepth( texCoord + vec2(        xi, yi ) );
    checkDepth( texCoord + vec2(   2. * xi, yi ) );

    checkDepth( texCoord + vec2( - 2. * xi, 2. * yi ) );
    checkDepth( texCoord + vec2(      - xi, 2. * yi ) );
    checkDepth( texCoord + vec2(        0., 2. * yi ) );
    checkDepth( texCoord + vec2(        xi, 2. * yi ) );
    checkDepth( texCoord + vec2(   2. * xi, 2. * yi ) );

    occlusion /= 24.;
    occlusion += 0.02 * random( vec3( gl_FragCoord.xy, depth ), length( gl_FragCoord ) );

    if( onlyOcclusion == 1.0) {
	gl_FragColor = vec4( vec3( occlusion ), 1. );

    } else {
	vec3 color = texture2D( m_Texture, texCoord ).rgb;
	color = mix( vec3( 0. ), color, occlusion );
	gl_FragColor = vec4( color, 1. );
    }


    //float inBlack = 0.;
   // float inWhite = 255.;
   // float inGamma = 10.;
  //  float outBlack = 0.;
  //  float outWhite = 255.;

    ////occlusion = ( pow( ( ( occlusion * 255.0) - inBlack) / (inWhite - inBlack), inGamma) * (outWhite - outBlack) + outBlack) / 255.0;

   // gl_FragColor = vec4( vec3( occlusion ), 1. );

}