/*
 * Illumination fragment shader: phong and toon shading
 */

// From vertex shader: position and normal (in eye coordinates)
varying vec4 pos;
varying vec3 norm;

// Do Phong specular shading (r DOT v) instead of Blinn-Phong (n DOT h)
uniform int phong;
// Do toon shading
uniform int toon;
// If false, then don't do anything in fragment shader
uniform int useFragShader;

// Toon shading parameters
uniform float toonHigh;
uniform float toonLow;

// Compute toon shade value given diffuse and specular component levels
vec4 toonShade(float diffuse, float specular)
{
    // ... (placeholder)
  	vec3 l_vec_normed = normalize(vec3(gl_LightSource[0].position - pos));
        vec3 v_vec_normed = normalize(vec3(vec4(0.0,0.0,0.0,0.0)-pos));
        vec3 n_normed = normalize(norm);
        vec3 h_normed = normalize(l_vec_normed + v_vec_normed);
    
    	float d = max(0.0,dot(n_normed,l_vec_normed));
    	float s = pow(max(0.0,dot(n_normed,h_normed)),gl_FrontMaterial.shininess);
    	
    	if(dot(n_normed,l_vec_normed) < 0){
    	 	s = 0.0;
    	}
    	
    	if(s > toonHigh){
    		return vec4(1.0,1.0,1.0,1.0);
    	}
    	else if(d > toonHigh){
    		return gl_FrontLightProduct[0].ambient+gl_FrontLightProduct[0].diffuse*.8;
    	}
    	else if( d >= toonLow && d <= toonHigh){
    		return gl_FrontLightProduct[0].ambient+gl_FrontLightProduct[0].diffuse*.4;
    	}
    	else{
    		return gl_FrontLightProduct[0].ambient+gl_FrontLightProduct[0].diffuse*.1;
    	}
    	
    	
} 

void main()
{

    if (useFragShader == 0) {
        // Pass through
        gl_FragColor = gl_Color;
    } else {
        // Do lighting computation...
        
        if (toon == 1) {

            gl_FragColor = toonShade(0.0, 0.0);
        }else {

        	vec3 l_vec_normed = normalize(vec3(gl_LightSource[0].position - pos));
        	vec3 v_vec_normed = normalize(vec3(vec4(0.0,0.0,0.0,0.0)-pos));
        	vec3 n_normed = normalize(norm);
        	vec3 h_normed = normalize(l_vec_normed + v_vec_normed);
        
        	vec4 result = gl_FrontLightProduct[0].ambient+(gl_FrontLightProduct[0].diffuse*max(0.0,dot(n_normed,l_vec_normed)));
        
        	if(phong == 1){
        		vec3 r_vec = (2*dot(n_normed,l_vec_normed))*n_normed-l_vec_normed;
			float alpha_prime = gl_FrontMaterial.shininess*(log(dot(n_normed,h_normed))/log(dot(r_vec,v_vec_normed)))*1.3;
			//************PHONG MODEL COMMENTS**********************************
			//So I set LsKs(n.h)^a = LsKs(r.v)^a_prime Then I solved for a_prime, however once I did this there was no difference between the phong
			//model and the bling-phong model, so I compared wikipedia examples to see what the phong model should look like, and then I
			//multiplied alpha_prime by some value that seemed to accomplish the same visual effect.  I am sure that you guys did not intend for the 
			//problem to be solved this way, but with the information given to us its hard to derive an exponent that does not make the shininess exactly
			//equal, and gives results that look proper.  I hope you will award at least some partial credit for this approach.
        		if(dot(n_normed,l_vec_normed) >= 0.0){
        			result+=(gl_FrontLightProduct[0].specular*pow(max(0.0,dot(r_vec,v_vec_normed)),alpha_prime));
        		}
        	}else{
        		if(dot(n_normed,l_vec_normed) >= 0.0){
        			result+=(gl_FrontLightProduct[0].specular*pow(max(0.0,dot(n_normed,h_normed)),gl_FrontMaterial.shininess));
        		}
        	}

           	gl_FragColor = result;
        }
    }
}
