{
  description = "ISP-Gestionare-Adeverinte dev environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
        jdk = pkgs.zulu25;
      in
      {
        devShells.default = pkgs.mkShell {
          packages = [
            jdk
            pkgs."go-task"
          ];

          shellHook = ''
            export JAVA_HOME=${jdk}
          '';
        };
      });
}
