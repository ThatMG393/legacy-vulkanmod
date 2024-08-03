{ pkgs, ... }: {
  channel = "stable-23.11";

  packages = [
    pkgs.temurin-bin-17
    pkgs.mesa
    pkgs.mesa_drivers
    pkgs.xvfb-run
    pkgs.mesa-demos
    pkgs.novnc
    pkgs.hostname-debian
  ];

  idx = {
    extensions = [
      "redhat.java"
      "vscjava.vscode-java-debug"
      "vscjava.vscode-java-dependency"
      "vscjava.vscode-java-pack"
      "vscjava.vscode-java-test"
      "vscjava.vscode-maven"
      "DontShaveTheYak.groovy-guru"
    ];

    previews = {
      enable = true;
      previews = {
        web = {
          command = [
            "novnc"
            "--vnc"
            "0.0.0.0:5901"
            "--listen"
            "0.0.0.0:$PORT"
          ];
          manager = "web";
        };
      };
    };
  };
}
