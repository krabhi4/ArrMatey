//
//  TrackRow.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-12.
//

import SwiftUI
import Shared

struct TrackRow: View {
    let track: LidarrTrack
    let trackFile: LidarrTrackFile?
    
    private var file: LidarrTrackFile? {
        track.trackFile ?? trackFile
    }
    
    private var mediaInfo: MediaInfo? {
        file?.mediaInfo
    }
    
    var body: some View {
        HStack(alignment: .center, spacing: 8) {
            VStack(alignment: .leading, spacing: 2) {
                HStack(spacing: 4) {
                    Text("\(track.absoluteTrackNumber).")
                        .font(.system(size: 16))
                        .foregroundColor(.blue)
                    
                    Text(track.title)
                        .font(.system(size: 16, weight: .medium))
                        .lineLimit(1)
                        .truncationMode(.middle)
                }
                
                HStack(spacing: 4) {
                    Text(track.duration.formatAsDuration())
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                    
                    Text("•")
                        .foregroundColor(.secondary)
                    
                    Text(statusText.text)
                        .font(.system(size: 14).italic())
                        .foregroundColor(statusText.color)
                        .lineLimit(1)
                }
            }
            
            Spacer()
            
            if let condensed = condensedStatus {
                Text(condensed)
                    .font(.system(size: 10, weight: .bold))
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(4)
            } else {
                Image(systemName: "exclamationmark.triangle.fill")
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundColor(.red)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
    
    private var statusText: (text: String, color: Color) {
        if file == nil {
            return (MR.strings().missing.localized(), .red)
        }
        guard let info = mediaInfo else {
            return (MR.strings().no_media_info.localized(), .red)
        }
        
        let parts = [
            info.audioCodec,
            info.audioChannels?.description,
            info.audioBitrate,
            info.audioSampleRate,
            info.audioBits?.description
        ].compactMap { $0 }
        
        return (parts.joined(separator: " - "), .green)
    }
    
    private var condensedStatus: String? {
        guard let info = mediaInfo else { return nil }
        let codec = info.audioCodec ?? ""
        let bits = info.audioBits?.description ?? ""
        let combined = "\(codec) \(bits)".trimmingCharacters(in: .whitespaces)
        return combined.isEmpty ? nil : combined
    }
}
