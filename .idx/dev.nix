{ pkgs, ... }: {
  channel = "stable-23.11";

  packages = [
    pkgs.temurin-bin-17
  ];

  idx = {
    extensions = [
      "redhat.java"
      "vscjava.vscode-java-debug"
      "vscjava.vscode-java-dependency"
      "vscjava.vscode-java-pack"
      "vscjava.vscode-java-test"
      "vscjava.vscode-maven"
    ];

    previews = {
      enable = true;
      previews = {
        web = {
          command = [
            "./gradlew" "runClient"
          ];
          manager = "web";
        };
      };
    };
  };
}
