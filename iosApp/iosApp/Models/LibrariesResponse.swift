//
//  LibrariesResponse.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-16.
//

struct LibrariesResponse: Codable {
    let libraries: [Library]
    let licenses: [String:License]
}

struct Library: Codable, Identifiable, Hashable, Equatable {
    let uniqueId: String
    let name: String
    let artifactVersion: String?
    let description: String?
    let website: String?
    let licenses: [String]?
    let organization: Organization?
    let funding: [Funding]?
    let tag: String?
    let scm: Scm?
    let developers: [Developer]?
    
    var id: String { uniqueId }
}

struct License: Codable, Identifiable, Hashable, Equatable {
    let name: String
    let url: String?
    let licenseContent: String?
    let year: String?
    let spdxId: String?
    let hash: String
    let internalHash: String?
    
    var id: String { name }
}

struct Organization: Codable, Hashable, Equatable {
    let name: String
    let url: String?
}

struct Funding: Codable, Hashable, Equatable {
    let platform: String?
    let url: String?
}

struct Developer: Codable, Hashable, Equatable {
    let name: String?
    let organizationUrl: String?
}

struct Scm: Codable, Hashable, Equatable {
    let connection: String?
    let developerConnection: String?
    let url: String?
}
