<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# package-finder Changelog

## [Unreleased]

## [0.24.0] - 2025-07-18

### Added

- Double-click to copy npm/yarn/pnpm add commands

## [0.23.0] - 2025-07-16

### Changed

- Add "Configure" button to the error dialog shown when Nexus dependency query fails

## [0.22.0] - 2025-07-15

### Added

- Add support for Gradle Plugin search

## [0.21.0] - 2025-07-13

### Added

- Added loading animation in table when searching packages.

### Changed

- Replace OkHttp with IntelliJ HttpConnectionUtils for HTTP requests.
- Set connect and read timeouts to 10s, following OkHttp default behavior.
- Update icon.

### Fixed

- Prevent UI blocking during package search

## [0.13.0] - 2025-07-09

### Added

- Support for searching in Nexus Private Repositories.

## [0.9.0] - 2025-07-03

### Added

- Added support for selecting dependency scope

## [0.8.0] - 2025-07-02

### Changed

- Bump intellij platform version

## [0.7.0] - 2025-02-05

### Added

- Added support for selecting dependency format: Maven, Gradle (Groovy), and Gradle (Kotlin)

## [0.6.1] - 2025-01-27

### Changed

- Increase top toolbar height in NpmToolWindow and MavenToolWindow
- Update toolwindow icon color

### Fixed

- Handle null publisher and package details in NPM package display

## [0.6.0] - 2025-01-27

### Added

- Add npm package search functionality

### Changed

- Only show download actions for available artifacts for central dependencies

## [0.5.0] - 2025-01-26

### Added

- Added context menu to download artifacts (JAR, sources, Javadoc) for central dependencies
- Add "Open Containing Folder" action for local dependencies

### Changed

- Updated UI placeholders to reflect the new query format options
- Central repository is now listed first in the repository combobox for better user experience.

## [0.4.0] - 2025-01-23

### Added

- Use theme-based icons for better UI

### Changed

- Supported searching dependencies directly from local repository
- Changed tool window icon
- 替换阿里云云效 Maven 搜索 API 为 sonatype 官方 Maven 搜索 API

## [0.3.0] - 2025-01-20

### Added

- Added support for searching from "Central" repositories

## [0.2.0] - 2025-01-19

### Added

- Add copy-to-clipboard functionality for Maven dependencies

### Changed

- Improved data loading efficiency by loading data only when needed
- 添加单独的按钮，用来加载本地 Maven 仓库依赖数据

### Fixed

- 修复表格数据变化时当前页码未正确重置的问题

## [0.1.0] - 2025-01-18

### Added

- Added support for searching Maven packages from the local repository

### Fixed

- Resolve table refresh issue on prev/next pagination

## [0.0.1] - 2025-01-16

### Added

- Initial project scaffold
- Add Package Finder tool window UI
