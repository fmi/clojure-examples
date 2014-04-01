// Limitations:
//
// (function () {
    var camera, scene, renderer;
    var geometry, material, mesh;

    var init = function () {
        renderer = new THREE.CanvasRenderer();
        renderer.setSize( window.innerWidth, window.innerHeight );
        document.body.appendChild( renderer.domElement );

        camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 1, 1000 );
        camera.position.z = 800;

        scene = new THREE.Scene();

        geometry = new THREE.CubeGeometry( 200, 200, 200 );
        material = new THREE.MeshBasicMaterial( { color: 0x000000, wireframe: true, wireframeLinewidth: 2 } );

        mesh = new THREE.Mesh( geometry, material );
        scene.add( mesh );
    }

//     var update;
//     (update = function () {
//         scene.remove( mesh );
//         geometry = new THREE.CubeGeometry( 10, 300, 30 );
//         //geometry = new THREE.CubeGeometry( 200, 200, 200 );
//         mesh = new THREE.Mesh( geometry, material );
//         scene.add( mesh );
//     })();

//     var camera_direction = 1;
//     var animate_camera = function () {
//         camera.position.z += 3 * camera_direction;
//
//         if (camera.position.z > 800 || camera.position.z < 200) {
//             camera_direction *= -1;
//         }
//     };

    var animate = function () {

        requestAnimationFrame( animate );

        // Inspect:
        mesh
        mesh.rotation

        // Watch:
        mesh.rotation
        mesh.rotation.x = Date.now() * 0.003;
        mesh.rotation.y = Date.now() * 0.001;

//         animate_camera();

        renderer.render( scene, camera );
    };

    init();
    animate();

// })();
